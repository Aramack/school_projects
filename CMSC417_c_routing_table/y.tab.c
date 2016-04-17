#ifndef lint
static char const 
yyrcsid[] = "";
#endif
#include <stdlib.h>
#define YYBYACC 1
#define YYMAJOR 1
#define YYMINOR 9
#define YYLEX yylex()
#define YYEMPTY -1
#define yyclearin (yychar=(YYEMPTY))
#define yyerrok (yyerrflag=0)
#define YYRECOVERING() (yyerrflag!=0)
static int yygrowstack();
#define YYPREFIX "yy"
#line 2 "ru.y"
#include <stdio.h>
#include <sys/types.h>
#include <string.h>
#include <assert.h>
#include "es.h"
#include "ls.h"
#include "n2h.h"
    
  extern char *rutext;
  int ruerror(char *s);
  int ru_line_num = 1;
  /* ----- */

#line 22 "ru.y"
typedef union
{
  char str[1024];
  int n;
} YYSTYPE;
#line 40 "y.tab.c"
#define YYERRCODE 256
#define open_paren 257
#define close_paren 258
#define establish_link 259
#define teardown_link 260
#define update_link 261
#define token_node 262
#define token_port 263
#define token_cost 264
#define token_name 265
#define nl 266
#define name_t 267
#define number 268
const short yylhs[] = {                                        -1,
    0,    0,    0,    0,    2,    1,    3,    3,    3,    3,
    4,    5,    6,
};
const short yylen[] = {                                         2,
    0,    2,    5,    6,    1,    4,    0,    2,    2,    2,
   14,    3,    5,
};
const short yydefred[] = {                                      0,
    5,    0,    0,    0,    0,    0,    2,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,    0,
    0,    8,    9,   10,    6,    0,   12,    0,    0,    0,
    0,    0,    0,    3,    0,   13,    4,    0,    0,    0,
    0,    0,    0,    0,    0,   11,
};
const short yydgoto[] = {                                       3,
    4,    5,   12,   13,   14,   15,
};
const short yysindex[] = {                                   -253,
    0, -257,    0, -253, -258, -252,    0, -246, -250, -249,
 -254, -239, -254, -254, -254, -245, -248, -244, -241, -234,
 -238,    0,    0,    0,    0, -236,    0, -243, -235, -253,
 -233, -232, -253,    0, -230,    0,    0, -231, -227, -229,
 -226, -228, -224, -237, -223,    0,
};
const short yyrindex[] = {                                     33,
    0,    0,    0,   33, -216,    0,    0,    0,    0,    0,
 -216,    0, -216, -216, -216,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,    0,    0,    0,   33,
    0,    0,   33,    0,    0,    0,    0,    0,    0,    0,
    0,    0,    0,    0,    0,    0,
};
const short yygindex[] = {                                     -4,
    0,    0,   -1,    0,    0,    0,
};
#define YYTABLESIZE 43
const short yytable[] = {                                       7,
    8,    9,   10,    1,    8,    9,   10,   11,    2,   20,
    6,   22,   23,   24,   16,   17,   18,   19,   21,   26,
   25,   27,   28,   29,   32,   34,   31,   30,   37,   45,
   33,   38,    1,   36,   35,   40,   39,   42,   41,   43,
   44,    7,   46,
};
const short yycheck[] = {                                       4,
  259,  260,  261,  257,  259,  260,  261,  266,  262,   11,
  268,   13,   14,   15,  267,  262,  267,  267,  258,  268,
  266,  266,  264,  258,  268,   30,  263,  266,   33,  267,
  266,  262,    0,  266,  268,  263,  268,  264,  268,  268,
  265,  258,  266,
};
#define YYFINAL 3
#ifndef YYDEBUG
#define YYDEBUG 0
#endif
#define YYMAXTOKEN 268
#if YYDEBUG
const char * const yyname[] = {
"end-of-file",0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"open_paren","close_paren",
"establish_link","teardown_link","update_link","token_node","token_port",
"token_cost","token_name","nl","name_t","number",
};
const char * const yyrule[] = {
"$accept : ru",
"ru :",
"ru : node_line ru",
"ru : open_paren_n event_set close_paren nl ru",
"ru : open_paren_n nl event_set close_paren nl ru",
"open_paren_n : open_paren",
"node_line : token_node number name_t nl",
"event_set :",
"event_set : es_link event_set",
"event_set : td_link event_set",
"event_set : ud_link event_set",
"es_link : establish_link token_node number token_port number token_node number token_port number token_cost number token_name name_t nl",
"td_link : teardown_link name_t nl",
"ud_link : update_link name_t token_cost number nl",
};
#endif
#if YYDEBUG
#include <stdio.h>
#endif
#ifdef YYSTACKSIZE
#undef YYMAXDEPTH
#define YYMAXDEPTH YYSTACKSIZE
#else
#ifdef YYMAXDEPTH
#define YYSTACKSIZE YYMAXDEPTH
#else
#define YYSTACKSIZE 10000
#define YYMAXDEPTH 10000
#endif
#endif
#define YYINITSTACKSIZE 200
int yydebug;
int yynerrs;
int yyerrflag;
int yychar;
short *yyssp;
YYSTYPE *yyvsp;
YYSTYPE yyval;
YYSTYPE yylval;
short *yyss;
short *yysslim;
YYSTYPE *yyvs;
int yystacksize;
#line 150 "ru.y"
 
int ruerror(char *s) {
    fprintf (stdout,"ru:%s::token :<%s> :line number %d\n", s, rutext, 
	     ru_line_num);
    if (strlen(rutext) == 0){
	fprintf (stdout,"No EOF at end of file??\n");
    }
    return 0;
}

#line 176 "y.tab.c"
/* allocate initial stack or double stack size, up to YYMAXDEPTH */
static int yygrowstack()
{
    int newsize, i;
    short *newss;
    YYSTYPE *newvs;

    if ((newsize = yystacksize) == 0)
        newsize = YYINITSTACKSIZE;
    else if (newsize >= YYMAXDEPTH)
        return -1;
    else if ((newsize *= 2) > YYMAXDEPTH)
        newsize = YYMAXDEPTH;
    i = yyssp - yyss;
    newss = yyss ? (short *)realloc(yyss, newsize * sizeof *newss) :
      (short *)malloc(newsize * sizeof *newss);
    if (newss == NULL)
        return -1;
    yyss = newss;
    yyssp = newss + i;
    newvs = yyvs ? (YYSTYPE *)realloc(yyvs, newsize * sizeof *newvs) :
      (YYSTYPE *)malloc(newsize * sizeof *newvs);
    if (newvs == NULL)
        return -1;
    yyvs = newvs;
    yyvsp = newvs + i;
    yystacksize = newsize;
    yysslim = yyss + newsize - 1;
    return 0;
}

#define YYABORT goto yyabort
#define YYREJECT goto yyabort
#define YYACCEPT goto yyaccept
#define YYERROR goto yyerrlab

#ifndef YYPARSE_PARAM
#if defined(__cplusplus) || __STDC__
#define YYPARSE_PARAM_ARG void
#define YYPARSE_PARAM_DECL
#else	/* ! ANSI-C/C++ */
#define YYPARSE_PARAM_ARG
#define YYPARSE_PARAM_DECL
#endif	/* ANSI-C/C++ */
#else	/* YYPARSE_PARAM */
#ifndef YYPARSE_PARAM_TYPE
#define YYPARSE_PARAM_TYPE void *
#endif
#if defined(__cplusplus) || __STDC__
#define YYPARSE_PARAM_ARG YYPARSE_PARAM_TYPE YYPARSE_PARAM
#define YYPARSE_PARAM_DECL
#else	/* ! ANSI-C/C++ */
#define YYPARSE_PARAM_ARG YYPARSE_PARAM
#define YYPARSE_PARAM_DECL YYPARSE_PARAM_TYPE YYPARSE_PARAM;
#endif	/* ANSI-C/C++ */
#endif	/* ! YYPARSE_PARAM */

int
yyparse (YYPARSE_PARAM_ARG)
    YYPARSE_PARAM_DECL
{
    register int yym, yyn, yystate;
#if YYDEBUG
    register const char *yys;

    if ((yys = getenv("YYDEBUG")))
    {
        yyn = *yys;
        if (yyn >= '0' && yyn <= '9')
            yydebug = yyn - '0';
    }
#endif

    yynerrs = 0;
    yyerrflag = 0;
    yychar = (-1);

    if (yyss == NULL && yygrowstack()) goto yyoverflow;
    yyssp = yyss;
    yyvsp = yyvs;
    *yyssp = yystate = 0;

yyloop:
    if ((yyn = yydefred[yystate])) goto yyreduce;
    if (yychar < 0)
    {
        if ((yychar = yylex()) < 0) yychar = 0;
#if YYDEBUG
        if (yydebug)
        {
            yys = 0;
            if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
            if (!yys) yys = "illegal-symbol";
            printf("%sdebug: state %d, reading %d (%s)\n",
                    YYPREFIX, yystate, yychar, yys);
        }
#endif
    }
    if ((yyn = yysindex[yystate]) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
    {
#if YYDEBUG
        if (yydebug)
            printf("%sdebug: state %d, shifting to state %d\n",
                    YYPREFIX, yystate, yytable[yyn]);
#endif
        if (yyssp >= yysslim && yygrowstack())
        {
            goto yyoverflow;
        }
        *++yyssp = yystate = yytable[yyn];
        *++yyvsp = yylval;
        yychar = (-1);
        if (yyerrflag > 0)  --yyerrflag;
        goto yyloop;
    }
    if ((yyn = yyrindex[yystate]) && (yyn += yychar) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yychar)
    {
        yyn = yytable[yyn];
        goto yyreduce;
    }
    if (yyerrflag) goto yyinrecovery;
#if defined(lint) || defined(__GNUC__)
    goto yynewerror;
#endif
yynewerror:
    yyerror("syntax error");
#if defined(lint) || defined(__GNUC__)
    goto yyerrlab;
#endif
yyerrlab:
    ++yynerrs;
yyinrecovery:
    if (yyerrflag < 3)
    {
        yyerrflag = 3;
        for (;;)
        {
            if ((yyn = yysindex[*yyssp]) && (yyn += YYERRCODE) >= 0 &&
                    yyn <= YYTABLESIZE && yycheck[yyn] == YYERRCODE)
            {
#if YYDEBUG
                if (yydebug)
                    printf("%sdebug: state %d, error recovery shifting\
 to state %d\n", YYPREFIX, *yyssp, yytable[yyn]);
#endif
                if (yyssp >= yysslim && yygrowstack())
                {
                    goto yyoverflow;
                }
                *++yyssp = yystate = yytable[yyn];
                *++yyvsp = yylval;
                goto yyloop;
            }
            else
            {
#if YYDEBUG
                if (yydebug)
                    printf("%sdebug: error recovery discarding state %d\n",
                            YYPREFIX, *yyssp);
#endif
                if (yyssp <= yyss) goto yyabort;
                --yyssp;
                --yyvsp;
            }
        }
    }
    else
    {
        if (yychar == 0) goto yyabort;
#if YYDEBUG
        if (yydebug)
        {
            yys = 0;
            if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
            if (!yys) yys = "illegal-symbol";
            printf("%sdebug: state %d, error recovery discards token %d (%s)\n",
                    YYPREFIX, yystate, yychar, yys);
        }
#endif
        yychar = (-1);
        goto yyloop;
    }
yyreduce:
#if YYDEBUG
    if (yydebug)
        printf("%sdebug: state %d, reducing by rule %d (%s)\n",
                YYPREFIX, yystate, yyn, yyrule[yyn]);
#endif
    yym = yylen[yyn];
    yyval = yyvsp[1-yym];
    switch (yyn)
    {
case 1:
#line 51 "ru.y"
{
}
break;
case 2:
#line 55 "ru.y"
{
    /* identify myself*/
    if (is_me(get_myid()) == false) {
	printf("[ru] ==> given nodeid(%d)host(%s) is not localhost\n",
	        get_myid(), gethostbynode(get_myid()));
        exit(1);
    }

}
break;
case 3:
#line 66 "ru.y"
{
    /*printf ("[ru]\tparsed an event set\n");*/
}
break;
case 4:
#line 71 "ru.y"
{
    /*printf ("[ru]\tparsed an event set\n");*/
}
break;
case 5:
#line 78 "ru.y"
{
    printf ("\n\n[ru]\tcreating a new (empty) event set\n");
    add_new_es();
}
break;
case 6:
#line 86 "ru.y"
{
    static char init_done = false;

    if (init_done == false) {
	/* init node->hostname mapping*/
	/* init event list*/
	printf("[ru]\tInit structures...\n");
	create_n2h();
	init_new_el();
	init_done = true;
    }

    /* add to node_to_hostname*/
    assert (add_n2h(yyvsp[-2].n, yyvsp[-1].str));

    printf ("[ru]\tFound node %d %s\n", yyvsp[-2].n, yyvsp[-1].str);

}
break;
case 8:
#line 108 "ru.y"
{
}
break;
case 9:
#line 112 "ru.y"
{
}
break;
case 10:
#line 116 "ru.y"
{
}
break;
case 11:
#line 122 "ru.y"
{
    printf ("[ru]\tEstablish link between %d [%d] <--> %d [%d] cost %d name %s\n",
	    yyvsp[-11].n, yyvsp[-9].n, yyvsp[-7].n, yyvsp[-5].n, yyvsp[-3].n, yyvsp[-1].str);
    /* add to event set */
    /* peer0, port0, peer1, port1, cost, name */
    add_to_last_es(_es_link, yyvsp[-11].n, yyvsp[-9].n, yyvsp[-7].n, yyvsp[-5].n, yyvsp[-3].n, yyvsp[-1].str);
}
break;
case 12:
#line 132 "ru.y"
{
    printf ("[ru]\tTeardown link %s\n", yyvsp[-1].str);

    /* add to event set */
    /* peer0, port0, peer1, port1, cost, name */
    add_to_last_es(_td_link, -1, -1, -1, -1, -1, yyvsp[-1].str);
}
break;
case 13:
#line 141 "ru.y"
{
    printf ("[ru]\tUpdate link %s new cost %d\n", yyvsp[-3].str, yyvsp[-1].n);

    /* add to event set */
    /* peer0, port0, peer1, port1, cost, name */
    add_to_last_es(_ud_link, -1, -1, -1, -1, yyvsp[-1].n, yyvsp[-3].str);
}
break;
#line 473 "y.tab.c"
    }
    yyssp -= yym;
    yystate = *yyssp;
    yyvsp -= yym;
    yym = yylhs[yyn];
    if (yystate == 0 && yym == 0)
    {
#if YYDEBUG
        if (yydebug)
            printf("%sdebug: after reduction, shifting from state 0 to\
 state %d\n", YYPREFIX, YYFINAL);
#endif
        yystate = YYFINAL;
        *++yyssp = YYFINAL;
        *++yyvsp = yyval;
        if (yychar < 0)
        {
            if ((yychar = yylex()) < 0) yychar = 0;
#if YYDEBUG
            if (yydebug)
            {
                yys = 0;
                if (yychar <= YYMAXTOKEN) yys = yyname[yychar];
                if (!yys) yys = "illegal-symbol";
                printf("%sdebug: state %d, reading %d (%s)\n",
                        YYPREFIX, YYFINAL, yychar, yys);
            }
#endif
        }
        if (yychar == 0) goto yyaccept;
        goto yyloop;
    }
    if ((yyn = yygindex[yym]) && (yyn += yystate) >= 0 &&
            yyn <= YYTABLESIZE && yycheck[yyn] == yystate)
        yystate = yytable[yyn];
    else
        yystate = yydgoto[yym];
#if YYDEBUG
    if (yydebug)
        printf("%sdebug: after reduction, shifting from state %d \
to state %d\n", YYPREFIX, *yyssp, yystate);
#endif
    if (yyssp >= yysslim && yygrowstack())
    {
        goto yyoverflow;
    }
    *++yyssp = yystate;
    *++yyvsp = yyval;
    goto yyloop;
yyoverflow:
    yyerror("yacc stack overflow");
yyabort:
    return (1);
yyaccept:
    return (0);
}
